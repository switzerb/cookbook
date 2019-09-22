package com.brennaswitzer.cookbook.repositories;

import com.brennaswitzer.cookbook.domain.Label;
import org.springframework.data.repository.CrudRepository;

public interface LabelRepository extends CrudRepository<Label, Long> {

}
