package com.qyma.mi.work;



import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;

import java.io.StringReader;

public class SqlUtils2 {

    // 提取 SQL 语句中的表名
    public static String getTableName(String sql) throws Exception {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Statement statement = parserManager.parse(new StringReader(sql));

        if (statement instanceof Select) {
            return getTableNameFromSelect((Select) statement);
        } else if (statement instanceof Insert) {
            return ((Insert) statement).getTable().getName(); // 返回表名
        } else if (statement instanceof Update) {
            return ((Update) statement).getTable().getName(); // 返回表名
        } else if (statement instanceof Delete) {
            return ((Delete) statement).getTable().getName(); // 返回表名
        }

        throw new IllegalArgumentException("Unsupported SQL statement type");
    }

    // 处理 SELECT 语句，返回第一个表名
    private static String getTableNameFromSelect(Select select) {
        SelectBody selectBody = select.getSelectBody();
        return extractTableNameFromSelectBody(selectBody);
    }

    // 从 SelectBody 中提取第一个表名
    private static String extractTableNameFromSelectBody(SelectBody selectBody) {
        if (selectBody instanceof PlainSelect) {
            PlainSelect plainSelect = (PlainSelect) selectBody;
            String tableName = getTableNameFromFromItem(plainSelect.getFromItem());
            if (tableName != null) {
                return tableName;
            }

            // 如果有 JOIN 语句，取第一个 JOIN 表的表名
            if (plainSelect.getJoins() != null && !plainSelect.getJoins().isEmpty()) {
                for (Join join : plainSelect.getJoins()) {
                    FromItem joinItem = join.getRightItem();
                    tableName = getTableNameFromFromItem(joinItem);
                    if (tableName != null) {
                        return tableName;
                    }
                }
            }
        } else if (selectBody instanceof SetOperationList) {
            // 处理包含 UNION/INTERSECT/EXCEPT 等操作的 SELECT
            SetOperationList setOpList = (SetOperationList) selectBody;
            // 递归处理每个操作
            for (SelectBody subSelectBody : setOpList.getSelects()) {
                String tableName = extractTableNameFromSelectBody(subSelectBody);
                if (tableName != null) {
                    return tableName;
                }
            }
        }

        return null;
    }

    // 从 FromItem 中提取表名
    private static String getTableNameFromFromItem(FromItem fromItem) {
        if (fromItem instanceof Table) {
            Table table = (Table) fromItem;
            return table.getName(); // 返回表名
        }

        if (fromItem instanceof SubSelect) {
            // 递归处理子查询
            return extractTableNameFromSelectBody(((SubSelect) fromItem).getSelectBody());
        }

        return null; // 如果无法确定表名
    }


    public static void main(String[] args) throws Exception {
        // 测试用例1: 基本查询
        String sql1 = "SELECT * FROM `users`";
        System.out.println("Table Name in SQL1: " + SqlUtils2.getTableName(sql1));
        // Expected: users

        // 测试用例2: 带别名的查询
        String sql2 = "SELECT u.`id`,`name` FROM `users` u WHERE u.age > 25";
        System.out.println("Table Name in SQL2: " + SqlUtils2.getTableName(sql2));
        // Expected: users

        // 测试用例3: 子查询
        String sql3 = "SELECT * FROM (SELECT * FROM users) sub";
        System.out.println("Table Name in SQL3: " + SqlUtils2.getTableName(sql3));
        // Expected: users

        // 测试用例4: JOIN 查询
        String sql4 = "SELECT * FROM users u JOIN orders o ON u.id = o.user_id";
        System.out.println("Table Name in SQL4: " + SqlUtils2.getTableName(sql4));
        // Expected: users

        // 测试用例5: 嵌套子查询
        String sql5 = "SELECT * FROM users u WHERE u.id IN (SELECT user_id FROM orders o WHERE o.amount > 100)";
        System.out.println("Table Name in SQL5: " + SqlUtils2.getTableName(sql5));
        // Expected: users

        // 测试用例6: INSERT 语句
        String sql6 = "INSERT INTO users (id, name) VALUES (1, 'John Doe')";
        System.out.println("Table Name in SQL6: " + SqlUtils2.getTableName(sql6));
        // Expected: users

        // 测试用例7: UPDATE 语句
        String sql7 = "UPDATE users SET name = 'Jane Doe' WHERE id = 1";
        System.out.println("Table Name in SQL7: " + SqlUtils2.getTableName(sql7));
        // Expected: users

        // 测试用例8: DELETE 语句
        String sql8 = "DELETE FROM users WHERE id = 1";
        System.out.println("Table Name in SQL8: " + SqlUtils2.getTableName(sql8));
        // Expected: users
    }
}