package com.briup.cms.util.execl;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

//删除状态转换器   0|1  <---> 未被删除|被删除
public class DeletedConverter implements Converter<Integer> {

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
     * 将单元格里的数据转为java对象,也就是 被删除 转为1,未被删除 转成 0,用于导入excel时对逻辑删除字段进行转换
     * @param cellData            数据对象
     * @param contentProperty     单元格内容属性
     * @param globalConfiguration 全局配置对象
     * @return Integer
     */
    @Override
    public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return cellData.getStringValue().equals("被删除") ? 1 : 0;
    }

    /**
     * 将单元格里的数据转为java对象,也就是 被删除 转为1,未被删除 转成 0,用于导入excel时对逻辑删除字段进行转换
     * @param context 读取转换器上下文对象
     * @return Integer
     */
    @Override
    public Integer convertToJavaData(ReadConverterContext<?> context) {
        return context.getReadCellData().getStringValue().equals("被删除") ? 1 : 0;
    }

    /**
     * 在导出时,将逻辑删除字段中的 0 转换为 未被删除 1 转换为 被删除
     * @param value               逻辑删除字段的值 1为被删除 0为未被删除
     * @param contentProperty     单元格内容属性
     * @param globalConfiguration 全局配置对象
     */
    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return new WriteCellData<Integer>(value.equals(1) ? "被删除" : "未被删除");
    }

    /**
     * 在导出时,将逻辑删除字段中的 0 转换为 未被删除 1 转换为 被删除
     * @param context 编写转换器上下文对象
     */
    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Integer> context) {
        return new WriteCellData<Integer>(context.getValue().equals(1) ? "被删除" : "未被删除");
    }
}