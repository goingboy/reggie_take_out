package com.zsx.reggie.dto;

import com.zsx.reggie.entity.Setmeal;
import com.zsx.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
