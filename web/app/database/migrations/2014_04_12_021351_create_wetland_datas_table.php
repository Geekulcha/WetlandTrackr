<?php

use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateWetlandDatasTable extends Migration {

	/**
	 * Run the migrations.
	 *
	 * @return void
	 */
	public function up()
	{
		Schema::create('wetland_datas', function(Blueprint $table) {
            $table->engine = 'InnoDB';
            $table->increments('id');
            $table->decimal('lat');
            $table->decimal('lon');
            $table->string('picture');
            $table->text('desctiption');
            $table->integer('user_id')->unique();
            $table->timestamps();

        });
	}

	/**
	 * Reverse the migrations.
	 *
	 * @return void
	 */
	public function down()
	{
		Schema::drop('wetland_datas');
	}

}
