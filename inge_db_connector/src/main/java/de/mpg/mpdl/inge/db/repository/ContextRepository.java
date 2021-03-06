package de.mpg.mpdl.inge.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;

public interface ContextRepository extends JpaRepository<ContextDbVO, String> {
}
