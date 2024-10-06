// package com.moon.moonbank.controller;

// import net.sf.jasperreports.engine.JRException;
// import net.sf.jasperreports.engine.JasperExportManager;
// import net.sf.jasperreports.engine.JasperPrint;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.moon.lib.minio.MinioService;
// import com.moon.moonbank.model_elastic.OrderElastic;
// import com.moon.moonbank.repository_elastic.OrdersRepositoryElastic;
// import com.moon.moonbank.service.ReportService;

// import java.io.ByteArrayOutputStream;
// import java.util.List;
// import java.util.stream.Collectors;

// @RestController
// @RequestMapping("/report")
// public class ReportController {

//    @Autowired
//    private ReportService reportService;

//    @Autowired
//    private OrdersRepositoryElastic ordersRepositoryElastic;

//    @Autowired
//    private MinioService minioService;

//    @GetMapping("/generate")
//    public ResponseEntity<byte[]> generateReport() {
//       try {
//          // Ambil data OrderElastic
//          List<OrderElastic> data = fetchData();

//          // Generate laporan
//          JasperPrint jasperPrint = reportService.generateReport(data);

//          // Ekspor laporan ke PDF
//          ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//          JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

//          HttpHeaders headers = new HttpHeaders();
//          headers.setContentType(MediaType.APPLICATION_OCTET_STREAM );
//          headers.setContentDispositionFormData("attachment", "report.xlsx");

//          return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
//       } catch (JRException e) {
//          e.printStackTrace();
//          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//       }
//    }

//    private List<OrderElastic> fetchData() {
//       // Misalkan Anda ingin mengambil halaman pertama dengan ukuran 10
//       Pageable pageable = PageRequest.of(0, 10); // Halaman pertama, ukuran 10

//       // Mengambil data dari repository
//       Page<OrderElastic> resultPage = ordersRepositoryElastic.searchByKeyword("", pageable);

//       List<OrderElastic> modifiedResultPage = resultPage.stream()
//                .map(result -> {

//                   // Set URL customer pic
//                   // result.getCustomer().setPicUrl(minioService.getPublicLink(result.getCustomer().getPic()));

                  
//                   // Cek apakah semua item memiliki is_active == false
//                   boolean allItemsInactive = result.getItems().stream()
//                         .allMatch(item -> !item.getIsActive());

//                   // Jika semua item inactive, set isOrderActive pada order menjadi false
//                   result.setIsOrderActive(!allItemsInactive);

//                   // Filter item yang hanya is_active == true
//                   List<OrderElastic.Item> activeItems = result.getItems().stream()
//                         .filter(OrderElastic.Item::getIsActive) // get item hanya item yang aktif
//                         .collect(Collectors.toList());

//                   // Set jumlah item aktif (total_item) ke jumlah dari activeItems
//                   result.setTotalItem(activeItems.size());

//                   double totalOrderPrice = activeItems.stream()
//                         .mapToDouble(OrderElastic.Item::getTotalItemPrice) // Mengubah stream ke double stream
//                         .sum(); // Menjumlahkan semua nilai total_item_price dari item aktif

//                   result.setTotalOrderPrice(totalOrderPrice);

//                   result.setItems(activeItems); // Mengupdate daftar items di order

//                   return result;
//                })
//                .filter(OrderElastic::getIsOrderActive) // Filter hanya order yang aktif
//                .collect(Collectors.toList());

//       // Mengembalikan daftar OrderElastic
//       return modifiedResultPage;
//    }
// }


package com.moon.moonbank.controller;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moon.lib.minio.MinioService;
import com.moon.moonbank.model_elastic.OrderElastic;
import com.moon.moonbank.repository_elastic.OrdersRepositoryElastic;
import com.moon.moonbank.service.ReportService;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/report")
public class ReportController {

   @Autowired
   private ReportService reportService;

   @Autowired
   private OrdersRepositoryElastic ordersRepositoryElastic;

   @Autowired
   private MinioService minioService;

   @GetMapping("/generate")
public ResponseEntity<byte[]> generateReport() {
   try {
      // Ambil data OrderElastic
      List<OrderElastic> data = fetchData();

      // Generate laporan
      JasperPrint jasperPrint = reportService.generateReport(data);

      // Ekspor laporan ke PDF
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF); // Set ke PDF
      headers.setContentDispositionFormData("attachment", "report.pdf");

      return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
   } catch (JRException e) {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
   }
}


   private List<OrderElastic> fetchData() {
      Pageable pageable = PageRequest.of(0, 10); // Halaman pertama, ukuran 10
      Page<OrderElastic> resultPage = ordersRepositoryElastic.searchByKeyword("", pageable);

      List<OrderElastic> modifiedResultPage = resultPage.stream()
               .map(result -> {
                  // Cek apakah semua item memiliki is_active == false
                  boolean allItemsInactive = result.getItems().stream()
                        .allMatch(item -> !item.getIsActive());

                  // Jika semua item inactive, set isOrderActive pada order menjadi false
                  result.setIsOrderActive(!allItemsInactive);

                  // Filter item yang hanya is_active == true
                  List<OrderElastic.Item> activeItems = result.getItems().stream()
                        .filter(OrderElastic.Item::getIsActive) // get item hanya item yang aktif
                        .collect(Collectors.toList());

                  // Set jumlah item aktif (total_item) ke jumlah dari activeItems
                  result.setTotalItem(activeItems.size());

                  double totalOrderPrice = activeItems.stream()
                        .mapToDouble(OrderElastic.Item::getTotalItemPrice) // Mengubah stream ke double stream
                        .sum(); // Menjumlahkan semua nilai total_item_price dari item aktif

                  result.setTotalOrderPrice(totalOrderPrice);
                  result.setItems(activeItems); // Mengupdate daftar items di order

                  return result;
               })
               .filter(OrderElastic::getIsOrderActive) // Filter hanya order yang aktif
               .collect(Collectors.toList());

      return modifiedResultPage;
   }
}
