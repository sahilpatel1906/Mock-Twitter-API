package com.cooksys.team2socialmedia.dtos;

import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class HashtagDto {

    private String label;
    private Timestamp firstUsed;
    private Timestamp lastUsed;

}
