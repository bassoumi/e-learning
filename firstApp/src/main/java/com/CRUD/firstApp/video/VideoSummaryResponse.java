package com.CRUD.firstApp.video;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoSummaryResponse {
    private Long id;
    private String videoUrl;
    private String summaryText;
    private Integer contentId;
    private String contentTitle;

    public VideoSummaryResponse(VideoSummary vs) {
        this.id           = vs.getId();
        this.videoUrl     = vs.getVideoUrl();
        this.summaryText  = vs.getSummaryText();
        this.contentId    = vs.getContent().getId();
        this.contentTitle = vs.getContent().getTitle();
    }
}
