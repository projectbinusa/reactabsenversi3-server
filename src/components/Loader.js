import React from "react";
import { CircleLoader, ClockLoader, DotLoader, GridLoader, MoonLoader, PuffLoader, PulseLoader
} from 'react-spinners';


const Loader = () => (
<div className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-50 flex justify-center items-center">
  <div className="relative">
    <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2">
      <GridLoader color="#4c5dfc"size={20} />
    </div>
  </div>
</div>
);

export default Loader;