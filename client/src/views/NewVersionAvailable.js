import {
    Alert,
    Button,
} from "antd";
import React from "react";
import Dispatcher from "../data/dispatcher";
import WindowActions from "../data/WindowActions";

const NewVersionAvailable = () =>
    <Alert
        type="info"
        message={<div>
            A newer version of Foodinger is available.
            {" "}
            <Button
                size="small"
                type="primary"
                onClick={() => Dispatcher.dispatch({
                    type: WindowActions.LAUNCH_NEW_VERSION,
                })}>
                Update
            </Button>
            {" "}
            <Button
                size="small"
                onClick={() => Dispatcher.dispatch({
                    type: WindowActions.IGNORE_NEW_VERSION,
                })}>
                Ignore
            </Button>
        </div>}
        banner
        closable
        onClose={() => Dispatcher.dispatch({
            type: WindowActions.IGNORE_NEW_VERSION,
        })}
    />;

export default NewVersionAvailable;
