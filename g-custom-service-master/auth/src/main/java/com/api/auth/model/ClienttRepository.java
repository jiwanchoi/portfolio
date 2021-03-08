package com.api.auth.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienttRepository extends JpaRepository<Clientnfo, Long>{
	Optional<Clientnfo> findAllByClientid(String clientid);
	
	Optional<Clientnfo> findAllByClientidAndApino(String clientid,String apino);
}
