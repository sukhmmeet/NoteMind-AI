package com.dhaliwal.notemind.mapper.impl;

import com.dhaliwal.notemind.dto.TagDto;
import com.dhaliwal.notemind.entity.Tag;
import com.dhaliwal.notemind.mapper.TagMapper;
import org.springframework.stereotype.Component;

@Component
public class TagMapperImpl implements TagMapper {
    @Override
    public TagDto toDto(Tag tag) {
        if ( tag == null ) {
            return null;
        }

        TagDto tagDto = new TagDto();

        tagDto.setId( tag.getId() );
        tagDto.setName( tag.getName() );

        return tagDto;
    }

    @Override
    public Tag toEntity(TagDto tagDto) {
        if ( tagDto == null ) {
            return null;
        }

        Tag tag = new Tag();

        tag.setId( tagDto.getId() );
        tag.setName( tagDto.getName() );

        return tag;
    }
}
