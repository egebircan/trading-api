package com.example.demo.domain.repository;

import com.example.demo.domain.model.Asset;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByCustomerId(Long customerId);

    Optional<Asset> findByCustomerIdAndSymbol(Long customerId, String symbol);
}
