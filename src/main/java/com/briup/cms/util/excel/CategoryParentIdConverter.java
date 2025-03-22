package com.briup.cms.util.execl;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.briup.cms.bean.Category;
import com.briup.cms.exception.ServiceException;
import com.briup.cms.util.ResultCode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

//父栏目id转换器 id值 <-> 栏目名，如果id为null则转换为""
@Slf4j
public class CategoryParentIdConverter implements Converter<Integer> {

    //用于存放所有父栏目对象
    public static List<Category> pCateList;


    /**
     * 开启对Integer的支持
     * @return Integer.class
     */
    @Override
    public Class<?> supportJavaTypeKey() {
        return Integer.class;
    }

    /**
     * Excel文件中单元格的数据类型-String
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 将单元格里的数据转为java对象,也就是 将父栏目名称转换为父栏目ID,用于导入时使用
     * @param cellData            数据对象
     * @param contentProperty     单元格内容属性
     * @param globalConfiguration 全局配置对象
     * @return Integer
     */
    @Override
    public Integer convertToJavaData(ReadCellData<?> cellData,
                                     ExcelContentProperty contentProperty,
                                     GlobalConfiguration globalConfiguration) {
        String value = cellData.getStringValue();
        //借助list集合(所有父栏目对象),使用栏目name进行等值匹配，得到栏目id
        return converterStringToInteger(value);
    }

    /**
     * 将单元格里的数据转为java对象,也就是 将父栏目名称转换为父栏目ID,用于导入时使用
     * @param context 读取转换器上下文
     * @return Integer
     */
    @Override
    public Integer convertToJavaData(ReadConverterContext<?> context) {
        String value = context.getReadCellData().getStringValue();
        return converterStringToInteger(value);
    }

    //核心转换方法：借助list集合(所有父栏目对象),使用栏目name进行等值匹配，得到栏目id
    private Integer converterStringToInteger(String value) {
        //log.info("in converterStringToInteger, 表格父id值：" + value);

        if (value != null) {
            //获取所有父栏目的名称（ArrayList<Category> --> ArrayList<String name>）
            List<String> names =
                    pCateList.stream().map(Category::getName)
                                .collect(Collectors.toList());
            //将导入的数据中 父栏目名称 与 数据库中的父栏目名称进行对比, 如果存在则继续导入数据,不存在就抛出异常
            if (!names.contains(value)) {
                throw new ServiceException(ResultCode.PCATEGORY_IS_INVALID);
            }
            //从集合中根据 父栏目的名称 获取该名称 所对应的父栏目的 id
            int index = names.indexOf(value);
            //log.info("value: " + value + ", index: " + index);
            Integer parentId = pCateList.get(index).getId();
            //log.info("parentId: " + parentId);
            return parentId;
        }

        return null;
    }

    /**
     * 在导出时,将父栏目ID 转换为 父栏目名称
     * @param value               父栏目ID
     * @param contentProperty     单元格内容属性
     * @param globalConfiguration 全局配置对象
     */
    @Override
    public WriteCellData<?> convertToExcelData(Integer value,
                                               ExcelContentProperty contentProperty,
                                               GlobalConfiguration globalConfiguration) {
        return convertToExcelData(value);
    }

    /**
     * 在导出时,将父栏目ID 转换为 父栏目名称
     * @param context 读取转换器上下文对象
     * @return Integer
     */
    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Integer> context) {
        Integer value = context.getValue();
        return convertToExcelData(value);
    }

    //导出时，根据父栏目id 得到 父栏目name
    private WriteCellData<?> convertToExcelData(Integer value) {
        //log.info("in convertToExcelData,value: " + value);
        //1.如果栏目id为null，直接返回""到execl表格中
        if(value == null) {
            return new WriteCellData<>("");
        }

        //2.处理栏目id不为null的情况
        //a.在所有的父栏目中获取 栏目ID 与 待导出数据中父栏目ID 一致的 栏目【ID唯一】
        List<Category> categoryList = pCateList.stream()
                .filter(cate -> Objects.equals(cate.getId(), value))
                .collect(Collectors.toList());
        //b.如果集合不为空【有效cId】
        if (!categoryList.isEmpty()) {
            //导出的是父栏目的名称
            return new WriteCellData<>(categoryList.get(0).getName());
        }
        //c.集合为空，则说明 cId无效
        throw new ServiceException(ResultCode.PCATEGORY_IS_INVALID);
    }
}