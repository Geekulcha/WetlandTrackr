<?php

class Plant extends Eloquent {
	protected $fillable = array('name', 'description', 'picture');

	public static $rules = array();

    public function wetland_data() {
        return belongsTo('WetlandData');
    }
}
