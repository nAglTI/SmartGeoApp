package com.nagl.smartgeoapp.database.entity

import androidx.room.Ignore

abstract class Selectable(@Ignore var isSelected: Boolean = false)