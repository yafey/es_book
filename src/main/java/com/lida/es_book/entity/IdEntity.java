package com.lida.es_book.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass//标注为@MappedSuperclass的类不能再标注@Entity或@Table注解。
public abstract class IdEntity {
    @Id
    @GeneratedValue(generator = "esIdGenerator")
    @GenericGenerator(name = "esIdGenerator", strategy = "com.lida.es_book.entity.UniqueGenerator")
    protected String id;
}
