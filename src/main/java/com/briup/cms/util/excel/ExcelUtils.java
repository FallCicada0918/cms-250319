package com.briup.cms.util.execl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @Description 使用EasyExcel操作excel导入导出数据的工具类
 */
@Component
public class ExcelUtils {

    /**
     * 无需注册自定义类型转换时导入使用
     */
    @SneakyThrows
    public <T> List<T> importData(MultipartFile file, Class<T> tClass,
                                  AnalysisEventListener<T> listener) {
        return importData(file, tClass, listener, null);
    }

    /**
     * 从Excel文件中导入数据
     *
     * @param file       数据文件对象
     * @param tClass     待导入数据的类型
     * @param listener   自定义监听器
     * @param converter 自定义参数转换器
     * @param <T>        泛型
     * @return 导入的数据
     */
    @SneakyThrows
    public <T> List<T> importData(MultipartFile file, Class<T> tClass,
                                  AnalysisEventListener<T> listener, Converter<?> converter) {
        //获取文件的输入流
        @Cleanup//添加一个finally代码块进行关闭资源的操作
        InputStream in = file.getInputStream();

        //调用read方法
        ExcelReaderBuilder readerBuilder = EasyExcel.read(in)
                //注册自定义监听器，字段校验可以在监听器内实现
                .registerReadListener(listener)
                //对应导入的实体类
                .head(tClass);

        //注册自定义参数转换器
        if (converter != null) {
            readerBuilder.registerConverter(converter);
        }

        //读取 所有sheet页的数据
        return readerBuilder
                //列表头行数，1代表列表头有1行，第二行开始为数据行
                .headRowNumber(1)
                //开始读Excel，返回一个List<T>集合，继续后续入库操作
                .doReadAllSync();
    }

    /**
     * 无需注册自定义类型转换时导出使用
     */
    @SneakyThrows
    public <T> void exportExcel(
            HttpServletResponse response,
            List<T> list,
            Class<T> tClass,
            String fileName) {
        exportExcel(response, list, tClass, fileName, (Converter<?>[]) null);
    }

    /**
     * 导出数据到excel,单sheet表
     *
     * @param response   响应对象
     * @param list       待导出的数据
     * @param tClass     导出数据的类型
     * @param fileName   文件名称
     * @param converters 自定义参数转换器列表
     */
    @SneakyThrows
    public <T> void exportExcel(
            HttpServletResponse response,
            List<T> list,
            Class<T> tClass,
            String fileName, Converter<?>... converters) {
        //获取输出流
        @Cleanup
        OutputStream outputStream = response.getOutputStream();
        //设置响应结果
        this.setExcelResponseProp(response, fileName);

        //获取导出对象
        ExcelWriter excelWriter = getExcelWriter(outputStream, tClass, converters);

        //创建一个sheet表
        WriteSheet sheet =
                EasyExcel.writerSheet().build();
        //导出数据
        excelWriter.write(list, sheet);

        //导出完毕后刷新等收尾工作
        excelWriter.finish();
        outputStream.flush();
    }

    /**
     * 无需自定义类型转换时导出多sheet表使用
     */
    @SneakyThrows
    public <T> void exportManySheet(HttpServletResponse response, List<T> list, Class<T> tClass,
                                    String fileName) {
        exportManySheet(response, list, tClass, fileName, (Converter<?>[]) null);
    }

    /**
     * 导出数据到excel,多sheet表
     *
     * @param response  响应对象
     * @param list      待导出的数据
     * @param tClass    导出数据的类型
     * @param fileName  文件名称
     * @param converter 自定义参数转换器
     */
    @SneakyThrows
    public <T> void exportManySheet(HttpServletResponse response, List<T> list, Class<T> tClass,
                                    String fileName, Converter<?>... converter) {
        //获取输出流
        @Cleanup
        OutputStream outputStream = response.getOutputStream();
        //设置响应结果
        this.setExcelResponseProp(response, fileName);

        //获取导出对象
        ExcelWriter excelWriter = getExcelWriter(outputStream, tClass, converter);

        //将需要导出的数据进行分割,每1000条数据放入到一个sheet中
        int size = list.size();
        int total = 1;
        for (int i = 0; i < size; i += 1000) {
            //创建sheet对象,命名方式 如:日志信息1、日志信息2....
            WriteSheet writeSheet = EasyExcel.writerSheet(fileName + total++).build();
            //将数据写出到sheet中
            excelWriter.write(list.subList(i, size >= 1000 ? i + 1000 : size), writeSheet);
        }

        //导出完毕后刷新等收尾工作
        excelWriter.finish();
        outputStream.flush();
    }

    /**
     * 根据已有条件,创建导出对象
     */
    private <T> ExcelWriter getExcelWriter(OutputStream outputStream, Class<T> tClass,
                                           Converter<?>... converters) {
        //获取导出对象建造者
        ExcelWriterBuilder writerBuilder = EasyExcel.write(outputStream, tClass)
                .excelType(ExcelTypeEnum.XLSX);
        //注册自定义类型转换器
        if (converters != null) {
            for (Converter<?> converter : converters) {
                writerBuilder.registerConverter(converter);
            }
        }
        return writerBuilder.build();
    }

    /**
     * 设置响应结果
     *
     * @param response    响应结果对象
     * @param rawFileName 文件名
     * @throws UnsupportedEncodingException 不支持的编码异常
     */
    private void setExcelResponseProp(
            HttpServletResponse response,
            String rawFileName) throws UnsupportedEncodingException {
        //设置编码格式【必须小写 utf-8】如果设置“UTF-8”则swagger成功，但前后端联调出现Error
        response.setCharacterEncoding("utf-8");

        //设置导出文件名称（避免乱码）
        String fileName =
                URLEncoder.encode(rawFileName.concat(".xlsx"), "UTF-8");
        //设置内容类型
        response.setHeader("content-type", "application/octet-stream");
        //设置响应的编码格式
        response.setHeader("content-disposition",
                "attachment;filename=" + fileName);
    }
}