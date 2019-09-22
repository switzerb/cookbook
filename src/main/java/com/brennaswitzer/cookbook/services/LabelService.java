package com.brennaswitzer.cookbook.services;

import com.brennaswitzer.cookbook.domain.Label;
import com.brennaswitzer.cookbook.repositories.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabelService {

    @Autowired
    private LabelRepository labelRepository;

    public Label saveOrUpdateLabel(Label label) {
        return labelRepository.save(label);
    }

    public Iterable<Label> findAllLabels() {
        return labelRepository.findAll();
    }
}
