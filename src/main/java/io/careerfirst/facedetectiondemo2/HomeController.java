package io.careerfirst.facedetectiondemo2;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

@Controller
public class HomeController {

    static {
        nu.pattern.OpenCV.loadShared();
    }

    private ResourceLoader resourceLoader;

    public HomeController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("ud",new UploadData());
        return "home";
    }

    @PostMapping("/")
    public String home(@ModelAttribute("ud") UploadData ud, Model model){
        model.addAttribute("ud",new UploadData());

        try {
            File tempFile = File.createTempFile("image",".png");
            ud.getImage().transferTo(tempFile);
            //System.out.println(tempFile.getAbsolutePath());
            File fileOnServerWithFaceDetected = File.createTempFile("Detected",".png");
            detectFace(tempFile,fileOnServerWithFaceDetected);
            byte[] bytes = Files.readAllBytes(fileOnServerWithFaceDetected.toPath());
            String image = "data:image/png;base64,"+Base64.getEncoder().encodeToString(bytes);
            model.addAttribute("result",image);
            System.out.println(fileOnServerWithFaceDetected.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "home";
    }

    private void detectFace(File fileOnServer, File fileOnServerWithFaceDetected){

        try {
            String frontalFace = resourceLoader.getResource("classpath:lbpcascade_frontalface.xml")
                    .getFile()
                    .getAbsolutePath();
            // classfier - this will tell where is face
            CascadeClassifier faceDetector = new CascadeClassifier(frontalFace);
            // image covenrted in proper data structure
            Mat image = Imgcodecs.imread(fileOnServer.getAbsolutePath());
            //
            MatOfRect faceDetections = new MatOfRect();

            faceDetector.detectMultiScale(image,faceDetections);

            System.out.println("# of Face detected "+faceDetections.toArray().length);

            for(Rect rect : faceDetections.toArray()){
                Imgproc.rectangle(image,
                        new Point(rect.x,rect.y),
                        new Point(rect.x+rect.width,rect.y+rect.height),
                        new Scalar(255,255,255));
            }
            Imgcodecs.imwrite(fileOnServerWithFaceDetected.getAbsolutePath(),image);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
