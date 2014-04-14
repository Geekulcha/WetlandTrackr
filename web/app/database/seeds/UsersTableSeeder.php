<?php

use Faker\Factory as Faker;

class UsersTableSeeder extends Seeder {

	public function run()
	{
        $faker = Faker::create();

        // Wipe clean users table before populating
        User::truncate();

        // Create admin user
        User::create(array(
            'name'     => 'admin',
            'username' => 'admin',
            'email'    => 'admin@wetlandstrackr.dev',
            'password' => Hash::make('admin'),
        ));

        // Create 5 extra dummy users
        foreach(range(1, 5) as $index) {
            User::create(array(
                'name'     => $faker->name,
                'username' => $faker->userName,
                'email'    => $faker->email,
                'password' => Hash::make('test' . $index),
            ));
        }
	}

}
