package com.ivi.openlr.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.ivi.openlr.service.OpenlrService;
import openlr.OpenLRDecoder;
import openlr.binary.ByteArray;
import openlr.binary.OpenLRBinaryDecoder;
import openlr.binary.OpenLRBinaryEncoder;
import openlr.binary.data.RawBinaryData;
import openlr.binary.impl.LocationReferenceBinaryImpl;
import openlr.rawLocRef.RawLineLocRef;
import openlr.rawLocRef.RawLocationReference;
import org.bson.internal.Base64;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
public class OpenlrControllerTest {
    private static Logger logger = LoggerFactory.getLogger(OpenlrControllerTest.class);

    @Autowired
    MockMvc mvc;

    @Autowired
    OpenlrService openlrService;

    @Autowired
    WebApplicationContext ctx;

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(ctx).addFilters(
                new CharacterEncodingFilter("UTF-8", true)
        )
                .alwaysDo(print()).build();
    }

    @Test
    void lineLocation() throws Exception {
        // "POST" "http://localhost:7050/linelocation?
        String content = "{\n" +
                "  \"status\" : {\n" +
                "    \"angle\" : 180,\n" +
                "    \"speed\" : 100,\n" +
                "    \"accuracy\" : 100\n" +
                "  },\n" +
                "  \"route_types\" : [ \"scenic\" ],\n" +
                "  \"constraints\" : {\n" +
                "    \"unpaved\" : false,\n" +
                "    \"highway\" : false,\n" +
                "    \"ferry\" : false,\n" +
                "    \"tunnel\" : false\n" +
                "  },\n" +
                "  \"avoid_area\" : [ {\n" +
                "    \"vertices\" : [ {\n" +
                "      \"x\" : 126.917802,\n" +
                "      \"y\" : 37.496429\n" +
                "    }, {\n" +
                "      \"x\" : 126.917802,\n" +
                "      \"y\" : 37.496429\n" +
                "    }, {\n" +
                "      \"x\" : 126.917802,\n" +
                "      \"y\" : 37.496429\n" +
                "    }, {\n" +
                "      \"x\" : 126.917802,\n" +
                "      \"y\" : 37.496429\n" +
                "    } ]\n" +
                "  } ]\n" +
                "}";
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
//        // st=126.924556,37.493520&
//        param.add("st", "126.924556,37.493520");
//        // dt=127.028638,37.497770&
//        param.add("dt", "127.028638,37.497770");
        // 서울 테헤란
        param.add("st", "127.02850844771947,37.49787875986948");
        // 부산광역시 영도구 동삼2동 946-6
        param.add("dt", "129.081121,35.064846");
        // crs=WGS84" \
        param.add("crs", "WGS84");


        mvc.perform(
                post("/linelocation")
                        .characterEncoding("UTF-8")
                        //     -H 'Content-Type: application/json' \
                        .contentType(MediaType.APPLICATION_JSON)
                        //     -H 'Authorization: Bearer 4a182199-9315-4896-8db9-ce330ca83adf' \
                        .header("Authorization", "Bearer 4a182199-9315-4896-8db9-ce330ca83adf")
                        .queryParams((MultiValueMap<String, String>) param)
                        .content(content)
        )
                .andExpect(status().isOk())
                .andDo(new ResultHandler() {
                    @Override
                    public void handle(MvcResult mvcResult) throws Exception {
                        MockHttpServletResponse response = mvcResult.getResponse();
                        String content = response.getContentAsString();
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonNodeContent = objectMapper.readTree(content);
                        String encodedOpenlrStr = jsonNodeContent.at("/result_openlr").textValue();
                        byte [] openlrBytes = Base64.decode(encodedOpenlrStr);
                        logger.debug(String.format("-------length : %d-----------------------", openlrBytes.length));

                        OpenLRBinaryDecoder decoder = new OpenLRBinaryDecoder();
                        OpenLRBinaryEncoder encoder = new OpenLRBinaryEncoder();
                        RawBinaryData binaryData = decoder.resolveBinaryData("1", new ByteArray(openlrBytes));
                        LocationReferenceBinaryImpl locationReferenceBinary = new LocationReferenceBinaryImpl("1", new ByteArray(openlrBytes));
                        RawLineLocRef rawLocRef = (RawLineLocRef) decoder.decodeData(locationReferenceBinary);

                        for (int i = 0; i < rawLocRef.getLocationReferencePoints().size(); i++) {
                            logger.debug(String.format("[%2d] %s", i, rawLocRef.getLocationReferencePoints().get(i).toString()));
                        }

                    }
                })
                .andExpect(jsonPath("result_openlr", Matchers.notNullValue()))
        ;
        // .andExpect(jsonPath("result_openlr", Matchers.notNullValue("Cxqpf1pB8iigAP+oAEQtogL/8gBgLaAB/MUBTS2iEP0IExoqok0C7BS4KKBUABwA7C2iAwUJ/awqoBkAPQAjLaAB//sADSgA")))
    }
}