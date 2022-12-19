package team.creative.itemphysiclite;

import team.creative.creativecore.common.config.api.CreativeConfig;

public class ItemPhysicLiteConfig {
    
    @CreativeConfig
    @CreativeConfig.DecimalRange(min = 0, max = 10)
    public float rotateSpeed = 1.0F;
    
}
