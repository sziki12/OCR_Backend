package enumeration

import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler

enum class Category {
    Housing,
    Clothing,
    Food,
    Personal,
    Utilities,
    Household,
    Entertainment,
    Other,
    Undefined;
    companion object
    {
        fun getValidCategories():List<Category>
        {
            val list = ArrayList<Category>();
            for(item in Category.entries)
            {
                if(item != Undefined)
                    list.add(item);
            }
            return list
        }

        fun getValidCategoryNames():List<String>
        {
            val list = ArrayList<String>();
            for(item in Category.entries)
            {
                if(item != Undefined)
                    list.add(item.name);
            }
            return list
        }

        fun parse(value:String):Category
        {
            for(item in Category.entries)
            {
                if(item.name.lowercase() == value.lowercase())
                    return item
            }
            return Undefined
        }
    }
}