package org.hacker.mvc.model.test;

import java.io.File;
import java.util.Date;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

public class Blog {
  @NotBlank(message = "作者不能为null")
  @Length(min = 2, message = "autor最小为2")
  String autor;
  @NotBlank
  @Length(max = 10)
  String title;
  @NotBlank
  String content;
  @DateFormat(format = "yyyy-MM-dd", message="时间格式错误,必须为yyyy-MM-dd")
  Date createDate;
  @Range(min = 5, max = 10, message = "via_no")
  Integer via_no;

  public Blog(String autor, String title, String content, Date create_date, Integer via_no) {
    super();
    this.autor = autor;
    this.title = title;
    this.content = content;
    this.createDate = create_date;
    this.via_no = via_no;
  }

  public static void main(String[] args) {

    String src = "src.org/hack/controller";
    src = src.replaceAll("/", File.separator + File.separator);
    System.out.println(src);
    System.out.println("src\\org\\hack\\controller");
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    System.out.println(factory.getMessageInterpolator().getClass());

    Validator validator = factory.getValidator();
    Blog blog = new Blog("1", "2121", "212", new Date(), 2);

    Set<ConstraintViolation<Blog>> constraintViolations = validator.validate(blog);
    System.out.println(validator.getClass());
    for (ConstraintViolation<Blog> c : constraintViolations) {
      System.out.println(c.getInvalidValue() + ":" + c.getMessage());
    }
  }

}
