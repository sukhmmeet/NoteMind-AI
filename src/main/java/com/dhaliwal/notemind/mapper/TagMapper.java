package com.dhaliwal.notemind.mapper;


import com.dhaliwal.notemind.dto.TagDto;
import com.dhaliwal.notemind.entity.Tag;
import org.mapstruct.Mapper;

public interface TagMapper {
    TagDto toDto(Tag tag);
    Tag toEntity(TagDto tagDto);
}
