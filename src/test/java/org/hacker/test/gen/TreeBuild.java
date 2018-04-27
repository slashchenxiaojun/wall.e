package org.hacker.test.gen;

import org.hacker.dataschema.DataSchemaService;
import org.hacker.dataschema.Tree;

/**
 * Created by mr.j on 2018/1/10.
 */
public class TreeBuild {
  public static void main(String[] args) {
    String json = "{\n" +
            "  \"age\": {\n" +
            "    \"type\": \"int\",\n" +
            "    \"desc\": \"年龄\",\n" +
            "    \"min\": 0,\n" +
            "    \"max\": 100,\n" +
            "    \"required\": true\n" +
            "  },\n" +
            "  \"name\": {\n" +
            "    \"type\": \"string\",\n" +
            "    \"desc\": \"姓名\",\n" +
            "    \"min\": 3,\n" +
            "    \"max\": 10,\n" +
            "    \"pattern\": \"\\\\w{3, 10}\",\n" +
            "    \"required\": true\n" +
            "  },\n" +
            "  \"sex\": {\n" +
            "    \"type\": \"bool\",\n" +
            "    \"desc\": \"性别[true=男,false=女]\"\n" +
            "  },\n" +
            "  \"accountMoney\": {\n" +
            "    \"type\": \"number\",\n" +
            "    \"desc\": \"账户余额\"\n" +
            "  },\n" +
            "  \"gateway\": {\n" +
            "    \"type\": \"object\",\n" +
            "    \"desc\": \"用户网关\",\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"string\",\n" +
            "        \"desc\": \"网关id\"\n" +
            "      },\n" +
            "      \"name\": {\n" +
            "        \"type\": \"string\",\n" +
            "        \"desc\": \"网关备注\"\n" +
            "      },\n" +
            "      \"device\": {\n" +
            "        \"type\": \"object\",\n" +
            "        \"desc\": \"设备\",\n" +
            "        \"properties\": {\n" +
            "          \"id\": {\n" +
            "            \"type\": \"string\",\n" +
            "            \"desc\": \"设备id\"\n" +
            "          },\n" +
            "          \"name\": {\n" +
            "            \"type\": \"string\",\n" +
            "            \"desc\": \"设备备注\"\n" +
            "          },\n" +
            "          \"subDevice\": {\n" +
            "            \"type\": \"object\",\n" +
            "            \"desc\": \"子设备\",\n" +
            "            \"properties\": {\n" +
            "              \"id\": {\n" +
            "                \"type\": \"string\",\n" +
            "                \"desc\": \"设备id\"\n" +
            "              },\n" +
            "              \"name\": {\n" +
            "                \"type\": \"string\",\n" +
            "                \"desc\": \"设备备注\"\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"tag\": {\n" +
            "    \"type\": \"array\",\n" +
            "    \"items\": {\n" +
            "      \"type\": \"string\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"work\": {\n" +
            "    \"type\": \"enum\",\n" +
            "    \"items\": {\n" +
            "      \"A\": {\n" +
            "        \"desc\": \"设备备注\"\n" +
            "      },\n" +
            "      \"B\": {\n" +
            "        \"desc\": \"设备备注\"\n" +
            "      },\n" +
            "      \"C\": {\n" +
            "        \"desc\": \"设备备注\"\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"electric\": {\n" +
            "    \"type\": \"array\",\n" +
            "    \"items\": {\n" +
            "      \"type\": \"object\",\n" +
            "      \"properties\": {\n" +
            "        \"id\": {\n" +
            "          \"type\": \"string\",\n" +
            "          \"desc\": \"电表id\"\n" +
            "        },\n" +
            "        \"subElectric\": {\n" +
            "          \"type\": \"object\",\n" +
            "          \"properties\": {\n" +
            "            \"id\": {\n" +
            "              \"type\": \"string\",\n" +
            "              \"desc\": \"电表id\"\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
    Tree tree = DataSchemaService.toTree(json);
    DataSchemaService.printlnTree(tree.getChildren(), 0);

    String str = "{\n" +
            "  \"electricBean\": {\n" +
            "    \"type\": \"object\",\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"string\",\n" +
            "        \"desc\": \"电表id\"\n" +
            "      },\n" +
            "      \"subElectric\": {\n" +
            "        \"type\": \"object\",\n" +
            "        \"properties\": {\n" +
            "          \"id\": {\n" +
            "            \"type\": \"string\",\n" +
            "            \"desc\": \"电表id\"\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    tree = DataSchemaService.toTree(str);
    DataSchemaService.printlnTree(tree.getChildren(), 0);
  }
}
