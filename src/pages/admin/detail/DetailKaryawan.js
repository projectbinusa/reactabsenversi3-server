import React, { useEffect, useState } from "react";
import Navbar from "../../../components/NavbarAdmin";
import Sidebar from "../../../components/SidebarUser";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowLeft } from "@fortawesome/free-solid-svg-icons";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import axios from "axios";
import { API_DUMMY } from "../../../utils/api";

function DetailKaryawan() {
  const { id } = useParams();
  const [user, setUser] = useState("");

  const getUserData = async () => {
    try {
      const res = await axios.get(
        `${API_DUMMY}/api/user/getUserBy/${id}`
      );
      setUser(res.data);
    } catch (error) {
      console.log(error);
    }
  };
  useEffect(() => {
    getUserData();
  }, []);
  return (
    <div className="flex flex-col h-screen">
      <div className="sticky top-0 z-50">
        <Navbar />
      </div>
      <div className="flex h-full">
        <div className="fixed">
          <Sidebar />
        </div>
        <div className=" sm:ml-64 content-page container p-8  ml-14 md:ml-64 mt-12">
          <div className="p-4">
            <div className="p-5 mt-5">
              {/* <!-- Card --> */}
              <div className="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
                {/* <!-- Header --> */}
                <div className="flex justify-between">
                  <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                    Detail Karyawan
                  </h6>
                </div>
                <div className="mt-2 text-left">
                  <hr />
                </div>
                <div className="mt-7 text-left">
                  <div className="grid md:grid-cols-2 md:gap-6">
                    <div className="relative z-0 w-full mb-6 group">
                      <input
                        type="text"
                        name="email"
                        id="email"
                        className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                        placeholder=" "
                        autoComplete="off"
                        value={user.email || ""}
                        onChange={() => {}}
                        required
                        readOnly
                      />
                      <label
                        htmlFor="email"
                        className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                      >
                        Email
                      </label>
                    </div>
                    <div className="relative z-0 w-full mb-6 group">
                      <input
                        type="text"
                        name="username"
                        id="username"
                        className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer capitalize"
                        placeholder=" "
                        autoComplete="off"
                        value={user.username || ""}
                        required
                        onChange={() => {}}
                        readOnly
                      />
                      <label
                        htmlFor="username"
                        className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                      >
                        Username
                      </label>
                    </div>

                    <div className="relative z-0 w-full mb-6 group">
                      <input
                        type="text"
                        name="id_jabatan"
                        id="id_jabatan"
                        className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer capitalize"
                        placeholder=" "
                        autoComplete="off"
                        value={user.jabatan?.namaJabatan || ""}
                        required
                        onChange={() => {}}
                        readOnly
                      />
                      <label
                        htmlFor="id_jabatan"
                        className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                      >
                        Jabatan
                      </label>
                    </div>
                    <div className="relative z-0 w-full mb-6 group">
                      <input
                        type="text"
                        name="id_shift"
                        id="id_shift"
                        className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer capitalize"
                        placeholder=" "
                        autoComplete="off"
                        value={user.shift?.namaShift || ""}
                        required
                        onChange={() => {}}
                        readOnly
                      />
                      <label
                        htmlFor="id_shift"
                        className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                      >
                        Shift
                      </label>
                    </div>
                  </div>

                  <div className="text-left mt-4">
                    <div className="relative z-0 w-full mb-6 group">
                      <img
                        className="max-w-xs max-h-64 mb-6 ml-12"
                        src={
                          user.fotoUser
                            ? user.fotoUser
                            : "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAALcAAACUCAMAAADmiEg1AAAAllBMVEXw8PAAAAD4+Pj09PQNFSYACiDZ2ts8PD25urv///+kpaYAFigAECQAABkAABUAEyYAAA4AAAjl5OTp6urR0tOMjY98gIMAABx2d3hZW2A2O0UjKzaeoKIfIixFRERgZWtQU1dBRUpvb3HFxcYsLDOXmJkADRkoKyxMTU41NTVjZmcaIC4iIyavs7QwMzoYGx8VGiQPEhgcRQ3YAAAE20lEQVR4nO2af3OiPBDHya5BIiHhl4CoVUCvrVavz/t/c0+wdzO9u1YoXE16k88fjtPizNd1881mN45jsVgsFovFYrFYLBaLxWKxWCyWLw2+oFvGh0DAJHZb4kS91y2nJwixtyoW5fK4LBfFynO/hHJkabY9RmFAKac0CKPjNkuZ8cohqZ8CpTkPSUuYU0rFUx0z3cKugs6KRJKK9d1mf2rS5rTf3K0FlSFZOQaHHNyaSEnKzEsYACAAY4mXle0faxd0y3sPSLeRH4Rz97WHqPfuPBR+tE0NFY5upeRNG/xdH2JTEV9UrpGpgvFZ+GHtvBVVcOrQF8vYROGwJT7J3nE8ZBlRqWJgprC9kl2/u7EjFur/e+PsENIdF4sriYDxQvCdcWsTM0HXzTVV0KypKG4mqB/YrHkwvx5MmAd83Ri2NOeE7zp8Dt0dJ/Mb6ekHxiUNs65Fx7KQlkZ5ITbKLDo3cnDVU0YlCqg0od0ex6hKFJMcBaq8O00uiZJXJlk4CE5O3YGEE+HCpHgnKnHT7sTFVD0X30BPTzBu9fTQ3fO5W2F13xbsm9+uWbodiCTxeviJR2RkkGyHlXmU9dCdRfRslH8rQcse+86S9vl6twO9Pol7WZaeSXmC8VFVHp314J7wo0nL0rnU3/cdkjB55CS7kZ6egDfhdN9x3tlTPunhOrcEk4Og5VULx7Sk4pCYlSYq4N+lqK80L9EphPxuWLgVrIj8a4cCdbTwo8Ik8/4BTIVPZu80fhBnxBdT46LttJ2fc+CTffJmfzDZEz84G9f1uYDefSC/1e6fucDc+psMnozacl4B3n0ug8me/ZIsiGw1CWR+b96a/AlLHyIuyXHmJgiA2L4k7uxIJI8eUgPX5E8gLp5Dn5LneuU1aZo23qp+JtQXz0VsbLRb0Gk2JPC5IOG6rMp1SAT3A7LwTB5LXVDKS0ICzmkL5wEhZWO86hZgyWwzfdyt1+vd43QzS5jRKfIKBOa4jed5jesww6fcwH7xi4uZwG8XIsC8yLPm0FHGtv6+acwyQ3Tmofivo0OI6b0I5yYtUYgPRFLRsSFi+kwlOZhj5JBuVbE3mXVFEk5ClYzGzLshrZSch6vDtB9Pvsy7zRCOl/q17vXzY1y39awJg3p0d8IPs54ujU4W+qJr8nYDMNmq81lf2e2WdBnUa3cVyFrZH5ChIq6E655OwYnK8GPNBUwOoaQ9pkGfCKZTSqsP3kACt6LBtEe7/PNA9Zt3bTd/wk7KNzONujENJRnQE4GCyFBjwFkleDSgc4YJ4ULfABbaqfxsyAKDWTsQ0rU0YZEH1aB+NsYVzReadLdXk6LOYuptcBVpu7QE85xWA1dX21TO9Ww+av8IRDHUFLAQgZ5mODRLOhm87cFsQpc9St+/j/IE/jh4yoTuIx/mRWNJsjB/GO7B7IFGOi6JYbwIos7R3/uwLAyu3ZP8LNClnIxIUPAIn2g4P4y923AZHuvQ3Y62Rxxb0NFzKQ9ORJIxpRFTn9dwemhtMBilO+h1B+5v0+qWo3RzLQaudNNxuuUX1a0v3r7VfTNe8gSHoy+/uZ+4w0l8Xbp9fjcdzh0feKgerVtKOgYpdejG9Mwn4+BnHb0fTL2x6GlZtQPKcehuglssFovFYrFYLBaLxWKxWCwWi+Wf5X+cg0tu3tqVfwAAAABJRU5ErkJggg=="
                        }
                        alt="image user"
                      />
                      <label
                        htmlFor="foto"
                        className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                      >
                        Foto :
                      </label>
                      <br />
                      {/* <!-- Button --> */}
                      <div className="flex justify-between">
                        <a
                          className="focus:outline-none text-white bg-red-500 hover:bg-red-800 focus:ring-4 focus:ring-red-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 dark:bg-red-600 dark:hover:bg-red-700 dark:focus:ring-red-900"
                          href="/admin/karyawan"
                        >
                          <FontAwesomeIcon icon={faArrowLeft} />{" "}
                        </a>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default DetailKaryawan;
