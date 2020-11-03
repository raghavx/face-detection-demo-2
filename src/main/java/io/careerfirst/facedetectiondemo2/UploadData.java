package io.careerfirst.facedetectiondemo2;

import org.springframework.web.multipart.MultipartFile;

public class UploadData {

    private MultipartFile image;

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
