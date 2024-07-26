import React, { useEffect, useState } from "react";
import Navbar from "../../../components/NavbarSuper";
import Sidebar from "../../../components/SidebarUser";
import axios from "axios";
import { useHistory, useParams } from "react-router-dom/cjs/react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowLeft, faFloppyDisk } from "@fortawesome/free-solid-svg-icons";
import Swal from "sweetalert2";
import { API_DUMMY } from "../../../utils/api";

function EditJabatanSA() {
  const [jabatan, setJabatan] = useState("");
  const [namaJabatan, setNamaJabatan] = useState("");
  const { idJabatan } = useParams();
  const history = useHistory();

  useEffect(() => {
    axios
      .get(`${API_DUMMY}/api/jabatan/getbyid/` + idJabatan, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      })
      .then((ress) => {
        const response = ress.data;
        setNamaJabatan(response.namaJabatan);
      })
      .catch((error) => {
        console.log(error);
      });
  }, []);

  const updateAdmin = async (e) => {
    e.preventDefault();
    const jabatan = { namaJabatan: namaJabatan };

    try {
      const res = await axios.put(
        `${API_DUMMY}/api/jabatan/editById/${idJabatan}`,
        jabatan,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      setJabatan(res.data);
      Swal.fire("Berhasil", "Berhasil mengubah data", "success");
      history.push("/superadmin/jabatan");
    } catch (error) {
      console.log(error);
    }
  };
  return (
    <div className="flex flex-col h-screen">
      <div className="sticky top-0 z-50">
        <Navbar />
      </div>
      <div className="flex h-full">
        <div className="fixed">
          <Sidebar />
        </div>
      </div>
      <div className=" sm:ml-64 content-page p-8  ml-14 md:ml-64 mb-12">
        <div className="p-4">
          <div className="p-5 mb-44">
            {/* // <!-- Card --> */}
            <div className="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
              <div className="flex justify-between">
                <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                  Update Jabatan
                </h6>
              </div>

              <hr />

              <div className="mt-5 text-left">
                {/* <!-- Form Input --> */}
                <form onSubmit={updateAdmin}>
                  <div className="relative z-0 w-full mb-6 group">
                    <input
                      type="text"
                      name="nama_jabatan"
                      id="nama_jabatan"
                      className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                      placeholder=" "
                      autoComplete="off"
                      value={namaJabatan}
                      onChange={(e) => setNamaJabatan(e.target.value)}
                      required
                    />
                    <label
                      htmlFor="nama_jabatan"
                      className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                    >
                      Nama Jabatan
                    </label>
                  </div>

                  {/* <!-- Button --> */}
                  <div className="flex justify-between">
                      <a
                        className="focus:outline-none text-white bg-red-500 hover:bg-red-800 focus:ring-4 focus:ring-red-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 dark:bg-red-600 dark:hover:bg-red-700 dark:focus:ring-red-900"
                        href="/superadmin/jabatan"
                      >
                        <FontAwesomeIcon icon={faArrowLeft} />
                      </a>
                      <button
                        type="submit"
                        className="text-white bg-indigo-500 hover:bg-indigo-800 focus:ring-4 focus:ring-indigo-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 dark:bg-indigo-600 dark:hover:bg-indigo-700 focus:outline-none dark:focus:ring-indigo-800"
                      >
                        <FontAwesomeIcon icon={faFloppyDisk} />
                      </button>
                    </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default EditJabatanSA;
