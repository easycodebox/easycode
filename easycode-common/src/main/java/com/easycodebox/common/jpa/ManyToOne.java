/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://glassfish.dev.java.net/public/CDDLv1.0.html or
 * glassfish/bootstrap/legal/CDDLv1.0.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at glassfish/bootstrap/legal/CDDLv1.0.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */
package com.easycodebox.common.jpa;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation defines a single-valued association to another 
 * entity class that has many-to-one multiplicity. It is not normally 
 * necessary to specify the target entity explicitly since it can 
 * usually be inferred from the type of the object being referenced.
 *
 * <pre>
 *
 *     Example:
 *
 *     &#064;ManyToOne
 *     &#064;JoinColumn(name="CUST_ID", nullable=false, updatable=false)
 *     public Customer getCustomer() { return customer; }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)

public @interface ManyToOne {

    /** 
     * (Optional) The entity class that is the target of 
     * the association. 
     *
     * <p> Defaults to the type of the field or property 
     * that stores the association. 
     */
    @SuppressWarnings("rawtypes")
	Class targetEntity() default void.class;

    /** 
     * 此功能暂未实现
     */
    FetchType fetch() default FetchType.LAZY;

}
