package top.abeille.common.basic;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 修改人
     */
    @Column(name = "modifier")
    @CreatedBy
    private Long creator;
    /**
     * 修改时间
     */
    @Column(name = "modify_time")
    @CreatedDate
    private LocalDateTime createTime;
    /**
     * 修改人
     */
    @Column(name = "modifier")
    @LastModifiedBy
    private Long modifier;
    /**
     * 修改时间
     */
    @Column(name = "modify_time")
    @LastModifiedDate
    private LocalDateTime modifyTime;
}
