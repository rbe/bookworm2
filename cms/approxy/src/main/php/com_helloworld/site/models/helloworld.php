<?php
defined('_JEXEC') or die;

class HelloWorldModelHelloworld extends JModelItem
{

    /**
     * @var string message
     */
    protected $message;

    /**
     * Get the message
     *
     * @return  string  The message to be displayed to the user
     */
    public function getMsg()
    {
        if (!isset($this->message)) {
            $this->message = 'Hello World!';
        }
        return $this->message;
    }

}
