package io.omnimail.cms.api.rest;

import io.omnimail.cms.application.service.AssetService;
import io.omnimail.cms.domain.model.Asset;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Asset upload(@RequestParam("file") MultipartFile file) throws Exception {
        return assetService.upload(file.getInputStream(), file.getContentType(), file.getSize());
    }

    @GetMapping("/{hash}")
    public Map<String, Object> get(@PathVariable String hash) {
        Asset asset = assetService.get(hash);
        return Map.of(
                "contentHash", asset.contentHash(),
                "objectKey", asset.objectKey(),
                "mimeType", asset.mimeType(),
                "sizeBytes", asset.sizeBytes(),
                "cdnUrl", assetService.cdnUrl(asset));
    }
}
