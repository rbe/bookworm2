<?php
defined('_JEXEC') or die;

class HelloWorldModelHelloworldse extends JModelList
{
    protected function getListQuery()
    {
        $db = $this->getDbo();
        $query = $db->getQuery(true);
        $query->select($db->quoteName(array('id', 'title', 'introtext', 'photo')));
        $query->from($db->quoteName('#__location'));
        return $query;
    }
}
