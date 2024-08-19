package com.qyma.mi.work;




import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;



import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;


public class SqlUtils {



    /*
        修改 SELECT 语句以添加 WHERE 条件 sql中可以有子查询，但是只支持增强主select
     */
    public static String addInCondition(String sql, String tableAlias, String field, List<Long> data) throws Exception {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Statement statement = parserManager.parse(new StringReader(sql));

        if (statement instanceof Select) {
            Select select = (Select) statement;
            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();

            FromItem fromItem = plainSelect.getFromItem();
            String fromTable = fromItem.toString();
            String actualTableName = null;
            String actualAlias = null;

            // 处理表别名
            if (fromItem instanceof Table) {
                Table table = (Table) fromItem;
                actualTableName = table.getName();
                actualAlias = table.getAlias() != null ? table.getAlias().getName() : null;
            }

            if ((actualAlias != null && actualAlias.equalsIgnoreCase(tableAlias)) ||
                    (actualTableName != null && actualTableName.equalsIgnoreCase(tableAlias))) {
                Expression existingWhere = plainSelect.getWhere();

                // 创建 IN 条件的表达式列表
                List<Expression> expressions = data.stream()
                        .map(LongValue::new) // 将 Long 转换为 LongValue
                        .collect(Collectors.toList());

                ExpressionList expressionList = new ExpressionList(expressions);
                Expression newCondition = new InExpression(new Column(tableAlias + "." + field), expressionList);

                if (existingWhere != null) {
                    // 将新条件作为第一个条件，其余条件用 AND 连接
                    plainSelect.setWhere(new AndExpression(newCondition, existingWhere));
                } else {
                    // 创建新的 WHERE 子句
                    plainSelect.setWhere(newCondition);
                }

                // 生成新的 SQL
                return select.toString();
            }
        }

        return sql;
    }



    public static void main(String[] args) throws Exception {



        System.out.println("Modified SELECT: " + addInCondition("SELECT * FROM users WHERE age > 25", "users", "id", List.of(123L, 211L,111L))); // 输出: SELECT * FROM users WHERE age > 25 AND name IN ('Alice', 'Bob')
        System.out.println("Modified SELECT: " + addInCondition("SELECT * FROM users ", "users", "id", List.of(123L, 211L,111L)));
        System.out.println("Modified SELECT: " + addInCondition("SELECT * FROM \n users  \n WHERE age > 25 and id in (1,2,3,4,5)", "users", "id", List.of(123L, 211L,111L)));
        System.out.println("Modified SELECT: " + addInCondition("SELECT * FROM users WHERE age > 25 and id in (1,2,3,4,5)", "users", "id", List.of(123L, 211L,111L)));
//        System.out.println("Modified SELECT: " + addInCondition("SELECT * FROM 'users' WHERE age > 25 and id in (1,2,3,4,5)", "users", "id", List.of(123L, 211L,111L)));
        System.out.println("Modified SELECT: " + addInCondition("SELECT * FROM users u WHERE u.age > 25 ", "users", "id", List.of(123L, 211L,111L)));
        System.out.println("Modified SELECT: " + addInCondition("SELECT * FROM users2 WHERE age > 25 and id in (1,2,3,4,5)", "users", "id", List.of(123L, 211L,111L)));
        String sqlSelect2 = "SELECT o.customer_id, o.order_date "
                + "FROM orders o "
                + "WHERE o.customer_id IN ("
                + "    SELECT c.id "
                + "    FROM customers c "
                + "    WHERE c.aa < CURRENT_DATE"
                + ") "
                + "AND o.order_date > CURRENT_DATE";
        System.out.println("Modified SELECT: " + addInCondition(sqlSelect2, "orders", "id", List.of(123L, 211L,111L)));
        String sqlSelect3 = "SELECT o.customer_id, o.order_date "
                + "FROM orders o "
                + "WHERE o.customer_id IN (1,2,3) "
                + "AND o.order_date > CURRENT_DATE";
        System.out.println("Modified SELECT: " + addInCondition(sqlSelect3, "orders", "id", List.of(123L, 211L,111L)));
    }
}
