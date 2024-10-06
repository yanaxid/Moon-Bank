package com.moon.moonbank.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;


import com.moon.moonbank.model.MyModel;
import com.moon.moonbank.model_elastic.OrderElastic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import java.io.InputStream;

import java.io.FileInputStream;
import java.io.IOException;

@Service
public class ReportService {

    public JasperPrint generateReport(List<OrderElastic> data) throws JRException {
        // Load the JRXML file dari path spesifik
        String filePath = "C://79/MOONBANK/moonbank/src/main/resources/reports/report_template.jrxml";
        try (InputStream inputStream = new FileInputStream(filePath)) {
            // Compile the report
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

            // Fill the report with data
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
            Map<String, Object> parameters = new HashMap<>();
            // Tambahkan parameter jika diperlukan
            

            return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        } catch (IOException e) {
            throw new RuntimeException("File tidak ditemukan: " + filePath, e);
        }
    }
}

