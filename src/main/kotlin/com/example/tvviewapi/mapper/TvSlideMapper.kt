package com.example.tvviewapi.mapper

import com.example.tvviewapi.dto.TvSlideDto
import com.example.tvviewapi.entity.TvSlide
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface TvSlideMapper {
    fun toDto(tvSlide: TvSlide): TvSlideDto
    fun toEntity(tvSlideDto: TvSlideDto): TvSlide
}