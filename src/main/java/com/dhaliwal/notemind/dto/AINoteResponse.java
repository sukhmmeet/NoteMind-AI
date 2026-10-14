package com.dhaliwal.notemind.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AINoteResponse {
    private String summary;
    private List<String> tags;
}
