package com.example.clientapp.repo;

import com.example.clientapp.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.lib.Modular;

@Modular(name = "some-module")
public interface RequestRepository extends JpaRepository<Request, Long> {
}
