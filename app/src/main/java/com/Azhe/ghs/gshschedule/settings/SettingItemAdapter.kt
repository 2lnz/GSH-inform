package com.Azhe.ghs.gshschedule.settings

import com.chad.library.adapter.base.BaseProviderMultiAdapter
import com.Azhe.ghs.gshschedule.settings.items.BaseSettingItem
import com.Azhe.ghs.gshschedule.settings.provider.*

class SettingItemAdapter : BaseProviderMultiAdapter<BaseSettingItem>() {

    init {
        addItemProvider(CategoryItemProvider())
        addItemProvider(HorizontalItemProvider())
        addItemProvider(SeekBarItemProvider())
        addItemProvider(SwitchItemProvider())
        addItemProvider(VerticalItemProvider())
    }

    override fun getItemType(data: List<BaseSettingItem>, position: Int): Int {
        return data[position].getType()
    }

}