package com.ivi.ntg6stat.controller;

import com.ivi.ntg6stat.IviNtg6StatServiceApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class Ntg6StatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void get_auth_api() throws Exception {
        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();

//        "params": {
//            "sdate": "20200412",
//                    "edate": "20200508",
//                    "dtype": "day"
//        },

        info.add("sdate", "20200412");
        info.add("edate", "20200508");
        info.add("dtype", "day");

        // MockMvcRequestBuilders.
        mockMvc.perform(
                get("/stat")
                        .params(info)
                        .header("Authorization", "Bearer 4a182199-9315-4896-8db9-ce330ca83adf")
                        .header("Content-Type", "application/json"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
