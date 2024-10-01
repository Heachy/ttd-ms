package com.cy.generated.service;

import com.cy.generated.domain.Complain;

import java.util.List;

public interface ComplainService {

    boolean addComplain(Long complainantId, String taskId, String reason);

    public List<Complain> getComplainList();

}

