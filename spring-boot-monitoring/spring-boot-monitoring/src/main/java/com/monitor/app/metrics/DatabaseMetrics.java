package com.monitor.app.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Collection;

@Component
public class DatabaseMetrics {

    private final MeterRegistry meterRegistry;
    private final DataSource dataSource;
    private final Collection<DataSourcePoolMetadataProvider> metadataProviders;

    @Autowired
    public DatabaseMetrics(MeterRegistry meterRegistry, 
                          DataSource dataSource,
                          Collection<DataSourcePoolMetadataProvider> metadataProviders) {
        this.meterRegistry = meterRegistry;
        this.dataSource = dataSource;
        this.metadataProviders = metadataProviders;
    }

    @PostConstruct
    public void init() {
        registerDataSourceMetrics();
    }

    private void registerDataSourceMetrics() {
        DataSourcePoolMetadata metadata = getDataSourceMetadata();
        if (metadata != null) {
            // 活跃连接数
            Gauge.builder("db.connections.active", metadata, DataSourcePoolMetadata::getActive)
                 .description("当前活跃数据库连接数")
                 .tag("datasource", dataSource.getClass().getSimpleName())
                 .register(meterRegistry);
            
            // 最大连接数
            Gauge.builder("db.connections.max", metadata, DataSourcePoolMetadata::getMax)
                 .description("最大数据库连接数")
                 .tag("datasource", dataSource.getClass().getSimpleName())
                 .register(meterRegistry);
            
            // 最小连接数
            Gauge.builder("db.connections.min", metadata, DataSourcePoolMetadata::getMin)
                 .description("最小数据库连接数")
                 .tag("datasource", dataSource.getClass().getSimpleName())
                 .register(meterRegistry);
            
            // 空闲连接数
            if (metadata.getIdle() != null) {
                Gauge.builder("db.connections.idle", metadata, DataSourcePoolMetadata::getIdle)
                     .description("空闲数据库连接数")
                     .tag("datasource", dataSource.getClass().getSimpleName())
                     .register(meterRegistry);
            }
        }
    }

    private DataSourcePoolMetadata getDataSourceMetadata() {
        for (DataSourcePoolMetadataProvider provider : metadataProviders) {
            DataSourcePoolMetadata metadata = provider.getDataSourcePoolMetadata(dataSource);
            if (metadata != null) {
                return metadata;
            }
        }
        return null;
    }
}
