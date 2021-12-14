package com.yupfeg.base.data.mapper

/**
 * 数据类型映射接口
 * @param D dto数据传输类型
 * @param E entity实体持久化类型
 * */
interface DataMapper<D , E> {

    companion object {
        /**
         * 创建对应的数据映射，包含双向映射操作
         * @param toDto Entity映射为Dto类型
         * @param toEntity Dto映射为Entity类型
         */
        @Suppress("unused")
        fun <D,E>create(toDto : ((E?)->D)?= null, toEntity : ((D?)->E)? = null) : DataMapper<D, E> {
            return object : DataMapper<D, E> {
                override fun convertToEntity(input: D?): E {
                    toEntity?: throw NullPointerException("convertToEntity is null,check your code")
                    return toEntity(input)
                }

                override fun convertToDto(input: E?): D {
                    toDto?: throw NullPointerException("convertToDto is null,check your code")
                    return toDto(input)
                }
            }
        }
    }

    /**
     * 转化成为实体类对象
     * @param input dto数据传递类型
     * */
    fun convertToEntity(input : D?) : E

    /**
     * 转化为数据传递类对象
     * @param input 数据层的实体对象类型
     * */
    fun convertToDto(input : E?) : D
}