package com.briup.cms.util.execl;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.briup.cms.bean.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
    监听器内部方法执行时机：
        1.收到web请求，执行导入execl文件
        2.逐行转换（将每1行数据 转换为 Category对象）
            a.先执行监听器invoke方法，解析当前行数据
            b.invoke成功返回，则调用转换器convertToJavaData方法,实现 父栏目名 -> 栏目id值
            循环上述过程，逐行解析处理，直到所有行解析完成
        3.执行doAfterAllAnalysed方法，做额外的清理工作

   【后期功能优化】：导入的execl表中可包含2级栏目(其父栏目为新增的1级栏目)
*/
@Slf4j
@Component
public class CategoryListener extends AnalysisEventListener<Category> {
    //集合：数据库中所有栏目对象
    public static List<Category> cateList = null;
    //集合：存储所有栏目名(数据库中栏目名、要导入的execl文件中栏目名)
    List<String> names = new ArrayList<>();

    /**
     * 每解析一行，回调该方法
     * @param category 从excel中导入的栏目对象
     * @param context 分析上下文
     */
    @Override
    public void invoke(Category category, AnalysisContext context) {
        //1.第一次解析时，将数据库中所有栏目名称，添加到names集合中
        if(cateList != null) {
            names = cateList.stream().map(cate->cate.getName())
                    .collect(Collectors.toList());

            //添加成功，立即清理cateList集合
            cateList = null;
        }

        //2.获取当前行栏目名称，做数据校验
        String name = category.getName();
        //2.1 判断栏目名是否有效
        if (!StringUtils.hasText(name)) {
            throw new RuntimeException(String.format("第%s行栏目名称为空，请核实！", context.readRowHolder().getRowIndex() + 1));
        }
        //2.2 判断栏目名是否重复存在
        if (names.contains(name)) {
            throw new RuntimeException(String.format("第%s行栏目名称已重复，请核实！", context.readRowHolder().getRowIndex() + 1));
        }

        //3.添加当前行栏目名到names集合中
        names.add(name);
    }

    /**
     * 出现异常回调
     * @param exception 存在的异常
     * @param context 分析上下文
     * @throws Exception 抛出的异常
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        if (exception instanceof ExcelDataConvertException) {
            //从0开始计算
            int columnIndex =
                    ((ExcelDataConvertException) exception).getColumnIndex() + 1;
            int rowIndex =
                    ((ExcelDataConvertException) exception).getRowIndex() + 1;
            String message = "第" + rowIndex + "行，第" +
                    columnIndex + "列" + "数据格式有误，请核实";

            exception.printStackTrace();
            throw new RuntimeException(message);

        } else if (exception instanceof RuntimeException) {
            throw exception;
        } else {
            super.onException(exception, context);
        }
    }

    /**
     * 解析完,全部回调
     * @param context 分析上下文
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        //解析完,全部回调逻辑实现
        names.clear();
    }
}