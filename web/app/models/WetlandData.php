<?php

class WetlandData extends Eloquent {
	protected $fillable = array();

	public static $rules = array();

    public function plants() {
        return hasMany('Plant');
    }
}
