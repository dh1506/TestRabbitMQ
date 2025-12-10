package com.se445g.SE_445_G_ETL.service.interf;

public interface TargetTransformService {

    /**
     * Đẩy toàn bộ dữ liệu staging sang database target theo schema hr_target.
     */
    void loadToTarget();
}

