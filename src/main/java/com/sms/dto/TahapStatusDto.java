package com.sms.dto;

import com.sms.entity.Tahap1;
import com.sms.entity.Tahap2;
import com.sms.entity.Tahap3;
import com.sms.entity.Tahap4;
import com.sms.entity.Tahap5;
import com.sms.entity.Tahap6;
import com.sms.entity.Tahap7;
import com.sms.entity.Tahap8;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TahapStatusDto {
    private Long kegiatanId;
    private Tahap1 tahap1;
    private int tahap1Percentage;
    private Tahap2 tahap2;
    private int tahap2Percentage;
    private Tahap3 tahap3;
    private int tahap3Percentage;
    private Tahap4 tahap4;
    private int tahap4Percentage;
    private Tahap5 tahap5;
    private int tahap5Percentage;
    private Tahap6 tahap6;
    private int tahap6Percentage;
    private Tahap7 tahap7;
    private int tahap7Percentage;
    private Tahap8 tahap8;
    private int tahap8Percentage;
}