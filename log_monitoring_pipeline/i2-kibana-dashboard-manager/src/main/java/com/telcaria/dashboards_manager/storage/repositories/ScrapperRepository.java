package com.telcaria.dashboards_manager.storage.repositories;

import com.telcaria.dashboards_manager.storage.entities.Log;
import com.telcaria.dashboards_manager.storage.entities.Scrapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScrapperRepository extends JpaRepository<Scrapper, String>{
    //Optional<Scrapper> findBytopic(String topic);
}



