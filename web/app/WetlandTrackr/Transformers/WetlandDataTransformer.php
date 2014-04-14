<?php namespace WetlandTrackr\Transformers;


class WetlandDataTransformer extends Transformer {

    protected $userTransformer;
    protected $plantTransformer;

    function __construct(UserTransformer $userTransformer, PlantTransformer $plantTransformer)
    {
        $this->userTransformer = $userTransformer;
        $this->plantTransformer = $plantTransformer;
    }

    public function transform($wetlandData) {
        return array(
            'lat'         => $wetlandData['lat'],
            'lon'         => $wetlandData['lon'],
            'description' => $wetlandData['description'],
            'path_to_pic' => $wetlandData['picture'],
            // 'posted_by'   => $this->userTransformer->transform($wetlandData->user),
            'plants'      => $this->plantTransformer->transformCollection($wetlandData->plants->all())
        );
    }

} 