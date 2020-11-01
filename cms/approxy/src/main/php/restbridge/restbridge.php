<?php
defined('_JEXEC') or die('Restricted access');
error_reporting(-1);
ini_set('display_errors', 1);

class plgContentHelloworld extends JPlugin
{

    private $REST_ENDPOINTS = array(
        'AudiobookInfo' => array('url' => 'https://hoerbuchdienst.shard1.audiobook.wbh-online.de',
            'parameter_template' => '/{titelnummer}',
            'post_body_template' => '')
    );

    function __construct(&$subject, $params)
    {
        parent::__construct($subject, $params);
    }

    /**
     * @param $context
     * @param $article
     * @param $params
     * @param int $page
     * @return bool
     */
    public function onContentPrepare($context, &$article, &$params, $page = 0)
    {
        $regex =
            "/" .                      // delimiter
            "\\{" .                    // opening {
            "[\\s]*" .                 // skip whitespace
            "Bookworm" .               // required identifier
            "[\\s]*" .                 // skip whitespace
            ":" .                      // colon
            "[\\s]*" .                 // skip whitespace
            "([A-Za-z0-9_\\-]+)" .     // required command name
            "[\\s]*" .                 // skip whitespace
            "([A-Za-z0-9:,\s]{3,})*" . // parameter1:value1,parameter2:value2,...
            "[\\s]*" .                 // skip whitespace
            "\\}" .                    // closing }
            "/s";                      // delimiter
        $article->text = preg_replace_callback($regex, array($this, 'execute_command'), $article->text);
        return true;
    }

    /**
     * @param $matches
     * @return string
     */
    private function execute_command($matches)
    {
        $messages = array();
        // [0] => { Bookworm : HalloChristian titelnummer:12345,name:Ralf }
        // [1] => HalloChristian
        // [2] => titelnummer:12345,name:Ralf
        /* @var $command_name string */
        $command_name = $matches[1];
        $custom_module = $this->custom_module($command_name, $messages);
        /* @var $parameter_array string[][] */
        $parameter_array = $this->analyze_parameters($command_name, $matches[2], $messages);
        if ($messages) {
            return join("<br>\n", $messages);
        }
        /* @var $result array */
        $result = $this->call_method($command_name, $parameter_array);
        $hasMergableResult = is_array($result) && !empty($result);
        if ($hasMergableResult) {
            $content = $this->merge_template($custom_module, $result);
        } else if (is_array($result) && key_exists('message', $result)) {
            $content = $result['message'];
        } else {
            $content = '(no result)';
        }
        return $content;
    }

    /**
     * @param $command_name
     * @param $messages
     * @return stdClass
     */
    private function custom_module($command_name, &$messages)
    {
        /* @var $custom_module_title string */
        $custom_module_title = 'Bookworm_' . $command_name;
        /* @var $custom_module stdClass */
        $custom_module = Joomla\CMS\Helper\ModuleHelper::getModule('mod_custom', $custom_module_title);
        /* @var $custom_module_exists bool */
        $custom_module_exists = isset($custom_module) && is_object($custom_module) && !empty($custom_module->content);
        if (!$custom_module_exists) {
            $messages[] = "Command " . $command_name . ": Custom module title='" . $custom_module_title . "' not found";
        }
        return $custom_module;
    }

    /**
     * @param $command_name
     * @param $parameter_string
     * @param $messages
     * @return string[][]
     */
    private function analyze_parameters($command_name, $parameter_string, &$messages)
    {
        // [2] => titelnummer:12345,name:Ralf
        // [0] => titelnummer:12345
        // [1] => name:Ralf
        /* @var $parameters string[] */
        $parameters = explode(',', $parameter_string);
        /* @var $parameter_array string[][] */
        $parameter_array = array();
        foreach ($parameters as $p) {
            $key_value = explode(':', $p);
            if ($key_value && count($key_value) === 2) {
                $parameter_array[$key_value[0]] = $key_value[1];
            } else {
                $messages[] = "Command " . $command_name . ": Error analyzing parameter " . $p;
            }
        }
        return $parameter_array;
    }

    /**
     * @param $command_name string Name of command
     * @param $parameter_array string[][] Parameters (key/value)
     * @return array
     */
    private function call_method($command_name, $parameter_array)
    {
        $methodname = "cmd_" . $command_name;
        if (method_exists($this, $methodname)) {
            return $this->{$methodname}($parameter_array);
        } else {
            return array('message' => 'Method ' . $methodname . ' not found');
        }
    }

    /**
     * @param $custom_module stdClass
     * @param $values string[][]
     * @return string Content: template with {placeholder} substituted with value
     */
    private function merge_template($custom_module, $values)
    {
        $template = $custom_module->content;
        preg_match_all('/\{([A-Za-z_]+)*\}/', $template, $matches);
        // Array (
        //    [0] => Array (
        //           [0] => {titelnummer}
        //           [1] => {name}
        //           )
        //    [1] => Array (
        //           [0] => titelnummer
        //           [1] => name
        //           )
        // )
        $matches = $matches[1];
        $content = "";
        foreach ($values as $r) {
            $c = $template;
            foreach ($matches as $match) {
                if (key_exists($match, $r)) {
                    $value = $r[$match];
                } else { // TODO @JsonIgnoreUnknownProperties
                    $value = '(no value found for ' . $match . ')';
                }
                $c = str_replace('{' . $match . '}', $value, $c);
            }
            $content .= $c;
        }
        return $content;
    }

    /**
     * @param $parameters string[]
     * @return string[][]
     */
    private function cmd_HeyChristian($parameters)
    {
        return array(
            array(
                'titelnummer' => '1__' . $parameters['titelnummer'] . '__081542',
                'name' => $parameters['name']
            ),
            array(
                'titelnummer' => '2__' . $parameters['titelnummer'] . '__081542',
                'name' => $parameters['name']
            )
        );
    }

}
