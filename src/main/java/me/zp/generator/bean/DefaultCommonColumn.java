package me.zp.generator.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 描述：
 * 2021/5/27 9:36.
 *
 * @author zhangpeng2
 * @version 1.0
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
@ToString
public class DefaultCommonColumn extends CommonColumn {
}
