package com.csu.mypetstore.mapper;

import com.csu.mypetstore.domain.Sequence;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SequenceMapper {

    Sequence getSequence(Sequence sequence);
    void updateSequence(Sequence sequence);
}

