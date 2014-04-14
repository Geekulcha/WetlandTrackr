<?php

/**
 * Class ApiController
 */
class ApiController extends BaseController {

    /**
     * @var int
     */
    protected $statusCode = 200;

    /**
     * @param mixed $statusCode
     * @return $this
     */
    public function setStatusCode($statusCode)
    {
        $this->statusCode = $statusCode;

        return $this;
    }

    /**
     * @return mixed
     */
    public function getStatusCode()
    {
        return $this->statusCode;
    }

    /**
     * @param string $message
     * @return mixed
     */
    public function respondNotFound($message = "Not Found") {
        return $this->setStatusCode(404)->respondWithError($message);
    }

    /**
     * @param $data
     * @param array $headers
     * @return mixed
     */
    public function respond($data, $headers = array()){
        return Response::json($data, $this->getStatusCode(), $headers);
    }

    /**
     * @param $message
     * @return mixed
     */
    public function respondWithError($message) {
        return $this->respond(array(
            'error' => array(
                'message' => $message,
                'status_code' => $this->getStatusCode()
            )
        ));
    }

}