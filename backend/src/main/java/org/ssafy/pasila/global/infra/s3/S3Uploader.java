package org.ssafy.pasila.global.infra.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.ssafy.pasila.domain.apihandler.ErrorCode;
import org.ssafy.pasila.domain.apihandler.RestApiException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    // MultipartFile을 전달받아 File로 전환한 후 S3에 업로드
    // 추 후 같은 이미지 파일 이름을 보낼시 처리 하기
    public String upload(String id, MultipartFile multipartFile, String dirName) throws IOException {

        File uploadFile = convert(id, multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        return upload(uploadFile, dirName);

    }

    private String upload(File uploadFile, String dirName) {

        String fileName = dirName + "/" + uploadFile.getName();

        String uploadImageUrl = putS3(uploadFile, fileName);

        removeNewFile(uploadFile);  // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)

        return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환

    }

    private static String getUuid() {

        return UUID.randomUUID().toString().replaceAll("-", "");

    }

    //s3 파일 업로드
    private String putS3(File uploadFile, String fileName) {

        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)    // PublicRead 권한으로 업로드 됨
        );
        return amazonS3Client.getUrl(bucket, fileName).toString();

    }

    // 로컬에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {

        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }

    }

    // 로컬에 파일 저장
    private Optional<File> convert(String id, MultipartFile file) throws IOException {

        String originName = file.getOriginalFilename();

        assert originName != null;
        final String ext = originName.substring(originName.lastIndexOf("."));
//        final String saveFileName = getUuid() + ext;
        final String saveFileName = id + ext;

        File convertFile = new File(saveFileName);
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();

    }

    //s3 파일 삭제
    public void deleteImage(String originalFilename) {

        amazonS3Client.deleteObject(bucket, originalFilename);
        log.info("success : delete Image in S3");

    }

    //S3폴더 내 파일 리스트 전달
    public List<String> getFileList(String directory) {

        List<String> fileList = new ArrayList<>();

        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
                .withBucketName(bucket)
                .withPrefix(directory + "/"); //폴더 경로 지정

        ListObjectsV2Result result = amazonS3Client.listObjectsV2(listObjectsV2Request);
        List<S3ObjectSummary> objectSummaries = result.getObjectSummaries();

        for (S3ObjectSummary objectSummary : objectSummaries) {
            String key = objectSummary.getKey();
            if (!key.equals(directory + "/")) {
                fileList.add("https://" + bucket + ".s3"
                        + region + ".amazonaws.com/" + key);
            }
        }

        return fileList;

    }

}
