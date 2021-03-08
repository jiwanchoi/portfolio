package com.ivi.ntg6.mapper;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ivi.ntg6.model.Gclog;

public interface UserRepository extends MongoRepository<Gclog, Long>{
	
    public List<Gclog> findById(String id);
    public List<Gclog> findAll();
    
}
