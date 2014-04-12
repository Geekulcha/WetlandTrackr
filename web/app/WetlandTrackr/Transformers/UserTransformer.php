<?php namespace WetlandTrackr\Transformers;


class UserTransformer extends Transformer{

    public function transform($user)
    {
        return array(
            'full_name' => $user['name'],
            'username'  => $user['username'],
            'email' => $user['email'],
        );
    }
} 